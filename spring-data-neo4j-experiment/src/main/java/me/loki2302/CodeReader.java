package me.loki2302;

import me.loki2302.entities.ClassNode;
import me.loki2302.entities.ClassNodeRepository;
import me.loki2302.entities.MethodNode;
import me.loki2302.entities.MethodNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.io.File;

@Service
public class CodeReader {
    @Autowired
    private ClassNodeRepository classNodeRepository;

    @Autowired
    private MethodNodeRepository methodNodeRepository;

    public void readCode(File codeRoot) {
        SpoonAPI spoonAPI = new Launcher();
        spoonAPI.getEnvironment().setNoClasspath(true);
        spoonAPI.addInputResource(codeRoot.getAbsolutePath());
        spoonAPI.buildModel();

        // Construct class nodes, method nodes, links between methods and classes
        for(CtType<?> type : spoonAPI.getFactory().Class().getAll()) {
            String className = type.getQualifiedName();

            ClassNode classNode = new ClassNode();
            classNode.name = className;
            classNode.shortName = type.getSimpleName();

            for(CtMethod<?> method : type.getMethods()) {
                String methodName = String.format("%s#%s", className, method.getSimpleName());

                MethodNode methodNode = new MethodNode();
                methodNode.name = methodName;
                methodNode.shortName = method.getSimpleName();
                methodNode = methodNodeRepository.save(methodNode);

                classNode.ownedMethods.add(methodNode);
            }

            classNodeRepository.save(classNode);
        }

        // construct method-method usages
        for(CtType<?> type : spoonAPI.getFactory().Class().getAll()) {
            String thisClassName = type.getQualifiedName();

            for(CtMethod<?> method : type.getMethods()) {
                String thisMethodName = String.format("%s#%s", thisClassName, method.getSimpleName());
                MethodNode thisMethodNode = methodNodeRepository.findByName(thisMethodName);

                method.getBody().getElements((Filter<CtInvocation<?>>) e -> true).stream().forEach(invocation -> {
                    CtExecutableReference executableReference = invocation.getExecutable();
                    String executableMethodName = executableReference.getSimpleName();
                    CtTypeReference declaringType = executableReference.getDeclaringType();

                    String invokedMethodName = String.format("%s#%s", declaringType.getQualifiedName(), executableMethodName);
                    MethodNode invokedMethodNode = methodNodeRepository.findByName(invokedMethodName);

                    thisMethodNode.usedMethods.add(invokedMethodNode);
                });

                methodNodeRepository.save(thisMethodNode);
            }
        }

        // construct class-class usages
        for(CtType<?> type : spoonAPI.getFactory().Class().getAll()) {
            String className = type.getQualifiedName();
            ClassNode thisClassNode = classNodeRepository.findByName(className);

            for(CtField<?> field : type.getFields()) {
                String dependencyClassName = field.getType().getQualifiedName();

                ClassNode dependencyClassNode = classNodeRepository.findByName(dependencyClassName);
                thisClassNode.usedClasses.add(dependencyClassNode);
            }

            classNodeRepository.save(thisClassNode);
        }
    }
}

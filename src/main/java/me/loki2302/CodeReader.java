package me.loki2302;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CodeReader {
    @Autowired
    private ClassNodeRepository classNodeRepository;

    public void readCode(File codeRoot) {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        javaProjectBuilder.addSourceTree(codeRoot);

        for(JavaClass javaClass : javaProjectBuilder.getClasses()) {
            String name = javaClass.getName();
            ClassNode classNode = new ClassNode();
            classNode.name = name;
            classNodeRepository.save(classNode);
        }

        for(JavaClass javaClass : javaProjectBuilder.getClasses()) {
            String thisClassName = javaClass.getName();
            ClassNode thisClassNode = classNodeRepository.findByName(thisClassName);

            for(JavaField javaField : javaClass.getFields()) {
                JavaClass dependencyType = javaField.getType();
                String dependencyClassName = dependencyType.getName();

                ClassNode dependencyClassNode = classNodeRepository.findByName(dependencyClassName);
                thisClassNode.usedClasses.add(dependencyClassNode);
            }

            classNodeRepository.save(thisClassNode);
        }
    }
}

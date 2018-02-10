package com.loki2302;

import static org.junit.Assert.*;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:applicationContext.xml"})
public class HelloServiceTest {
	@Autowired
	HelloService helloServiceImplementation;
	
	private Server server;
	private HelloService client;
	
	@Before
	public void publish() {
		JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
		serverFactoryBean.setProvider(new JacksonJsonProvider());
		serverFactoryBean.setResourceClasses(HelloService.class);
		ResourceProvider resourceProvider = new SingletonResourceProvider(helloServiceImplementation);
		serverFactoryBean.setResourceProvider(HelloService.class, resourceProvider);
		serverFactoryBean.setAddress("http://localhost:9000/");
		BindingFactoryManager manager = serverFactoryBean.getBus().getExtension(BindingFactoryManager.class);
		JAXRSBindingFactory bindingFactory = new JAXRSBindingFactory();
		bindingFactory.setBus(serverFactoryBean.getBus());
		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, bindingFactory);
		server = serverFactoryBean.create();
		
		JAXRSClientFactoryBean clientFactoryBean = new JAXRSClientFactoryBean();
		clientFactoryBean.setResourceClass(HelloService.class);
		clientFactoryBean.setAddress("http://localhost:9000/");
		clientFactoryBean.setProvider(new JacksonJsonProvider());
		client = clientFactoryBean.create(HelloService.class);
	}
	
	@After
	public void unpublish() {
		server.stop();
		server = null;
		client = null;
	}
	
	@Test
	public void sayHelloTest() {		
		HelloDTO hello = client.sayHello("loki2302");
		assertNotNull(hello);
		assertEquals("Hello loki2302!", hello.message);
		assertEquals("loki2302", hello.name);
	}
	
	@Test
	public void addNumbersTest() {
		AddNumbersRequestDTO addNumbersRequest = new AddNumbersRequestDTO();
		addNumbersRequest.numberA = 2;
		addNumbersRequest.numberB = 3;
		AddNumbersResponseDTO response = client.addNumbers(addNumbersRequest);
		assertNotNull(response);
		assertEquals(2, response.numberA);
		assertEquals(3, response.numberB);
		assertEquals(5, response.result);
	}
}

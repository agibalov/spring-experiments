package com.loki2302;

import org.springframework.stereotype.Service;

@Service("helloService")
public class HelloServiceImpl implements HelloService {
	private int callCount = 0;
	
	public HelloDTO sayHello(String name) {
		HelloDTO dto = new HelloDTO();
		dto.name = name;
		dto.message = String.format("Hello %s!", name);
		dto.callCount = ++callCount;
		return dto;
	}    	
	
	public AddNumbersResponseDTO addNumbers(AddNumbersRequestDTO addNumbersRequest) {
		int numberA = addNumbersRequest.numberA;
		int numberB = addNumbersRequest.numberB;
		
		AddNumbersResponseDTO response = new AddNumbersResponseDTO();
		response.numberA = numberA;
		response.numberB = numberB;
		response.result = numberA + numberB;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		return response;
	} 
}
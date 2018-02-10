package com.loki2302;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("HelloService")
@Produces("application/json")
@Consumes("application/json")
public interface HelloService {
	@GET
	@Path("SayHello")
	HelloDTO sayHello(@QueryParam("name") String name);
	
	@POST
	@Path("AddNumbers")
	AddNumbersResponseDTO addNumbers(AddNumbersRequestDTO addNumbersRequest);
}
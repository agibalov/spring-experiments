package me.loki2302.cypher;

public class PersonDTO {
	long id;
	String name;
	
	@Override
    public String toString() {
    	return String.format("PersonDTO{id=%d,name=%s}", id, name);
    }
    
    @Override
	public int hashCode() {
        return new Long(id).hashCode();
	}
    
    @Override
    public boolean equals(Object obj) {
    	if(obj == null) {
    		return false;
    	}    	
    	
    	if(!(obj instanceof PersonDTO)) {
    		return false;
    	}
    	
    	PersonDTO otherPersonDto = (PersonDTO)obj;
    	return id == otherPersonDto.id && name.equals(otherPersonDto.name);
    }
}
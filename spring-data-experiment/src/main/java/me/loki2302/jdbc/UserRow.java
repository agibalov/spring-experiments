package me.loki2302.jdbc;

public class UserRow {
    public int UserId;
    public String Name;
    
    public Integer getId() {
        return UserId;
    }
    
    public void setUserId(Integer userId) {
        this.UserId = userId;
    }
            
    public String getName() {
        return Name;
    }
    
    public void setName(String name) {
        this.Name = name;
    }
}
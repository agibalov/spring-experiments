package me.loki2302.controllers;

import java.util.Collection;

import me.loki2302.service.Person;
import me.loki2302.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/person")
public class PersonController {
    @Autowired
    private PersonService personService;
    
    @RequestMapping
    public String people(Model model) {
        Collection<Person> people = personService.getPeople();
        model.addAttribute("people", people);
        return "person/person-list";            
    }
    
    @RequestMapping("{personId}")
    public String person(@PathVariable int personId, Model model) {
        Person person = personService.getPerson(personId);
        model.addAttribute("person", person);
        return "person/person";
    }
}
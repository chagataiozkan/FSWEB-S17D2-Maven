package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> findAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer findById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer save(@RequestBody Developer developer) {
        double salary = developer.getSalary();

        if (developer.getExperience() == Experience.JUNIOR) {
            salary = salary - (salary * taxable.getSimpleTaxRate() / 100);
            developer = new JuniorDeveloper(developer.getId(), developer.getName(), salary);
        } else if (developer.getExperience() == Experience.MID) {
            salary = salary - (salary * taxable.getMiddleTaxRate() / 100);
            developer = new MidDeveloper(developer.getId(), developer.getName(), salary);
        } else if (developer.getExperience() == Experience.SENIOR) {
            salary = salary - (salary * taxable.getUpperTaxRate() / 100);
            developer = new SeniorDeveloper(developer.getId(), developer.getName(), salary);
        }

        developers.put(developer.getId(), developer);
        return developer;
    }

    @PutMapping("/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer developer) {
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public Developer delete(@PathVariable int id) {
        return developers.remove(id);
    }
}

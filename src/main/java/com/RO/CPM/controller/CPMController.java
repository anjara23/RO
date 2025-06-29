package com.RO.CPM.controller;

import com.RO.CPM.model.Task;
import com.RO.CPM.service.CPMService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tache")
public class CPMController {

    @Autowired
    private CPMService cpmService;

    @PostMapping("/auto")
    public ResponseEntity<Map<String, List<String>>> saveTasks(@Valid @RequestBody List<@Valid Task> tasks) {
        Map<String, List<String>> result = cpmService.saveTasks(tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dateTot")
    public ResponseEntity<Map<String, Integer> > datePlusTot() {
        return ResponseEntity.ok(cpmService.datePlusTot());
    }

    @GetMapping("/critique")
    public ResponseEntity<List<String>> critique() {
        return ResponseEntity.ok(cpmService.cheminCritique());
    }

    @GetMapping("/dateTard")
    public ResponseEntity<Map<String, Integer> > datePlusTard() {
        return ResponseEntity.ok(cpmService.datePlusTard());
    }

    @GetMapping("/marge")
    public ResponseEntity<Map<String, Integer>> marge() {
        return ResponseEntity.ok(cpmService.marge());
    }

}

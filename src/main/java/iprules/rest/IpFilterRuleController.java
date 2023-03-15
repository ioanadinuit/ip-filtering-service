package iprules.rest;

import iprules.rest.dto.IpAllowedDTO;
import iprules.rest.dto.IpFilterRuleDTO;
import iprules.service.IpFilterRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/ip-filter-rules")
@RequiredArgsConstructor
public class IpFilterRuleController {
    private final IpFilterRuleService service;

    @PostMapping
    public ResponseEntity<IpFilterRuleDTO> create(@RequestBody IpFilterRuleDTO rule) {
        return new ResponseEntity<>(service.save(rule), CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<IpFilterRuleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping(value = "/allowed")
    public ResponseEntity<IpAllowedDTO> getById(
            @RequestParam String sourceIp,
            @RequestParam String destinationIp) {
        return ResponseEntity.ok(service.isAllowed(sourceIp, destinationIp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
         service.deleteById(id);
         return new ResponseEntity<>(HttpStatus.OK);
    }

}
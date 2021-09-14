package com.domain.item;

import java.util.List;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;

@RepositoryRestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemRestController {
    
    private final ItemRepository itemRepository;
    
    
    @GetMapping("")
    public @ResponseBody ResponseEntity<?> findAll() throws Exception {
        List<Item> list = itemRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> findOne(@PathVariable long id) throws Exception {
        Item item = itemRepository.findById(id).orElse(null);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<?> create(@RequestBody ItemDTO itemDTO) throws Exception {
        Item item = itemRepository.save(itemDTO.convertItem());
        return ResponseEntity.ok(item);
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public  @ResponseBody ResponseEntity<?> update(@PathVariable long id, @RequestBody ItemDTO itemDTO) throws Exception {
        Item item = itemRepository.findById(id).orElseThrow(() -> new Exception("대상을 찾을 수 없습니다"));
        item.setItem(itemDTO.getItem());
        item = itemRepository.save(item);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public  @ResponseBody ResponseEntity<?> delete(@PathVariable long id) throws Exception {
        Item item = itemRepository.findById(id).orElseThrow(() -> new Exception("대상을 찾을 수 없습니다"));
        itemRepository.delete(item);
        return ResponseEntity.noContent().build();
    }
}

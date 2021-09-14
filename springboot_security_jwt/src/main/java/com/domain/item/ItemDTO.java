package com.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
    private Long id;
    private String item;
    
    public Item convertItem () {
        return new Item(this.item);
    }
}

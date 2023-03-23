package com.example.yunhists.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateALotCat {

    List<Integer> categories;

    List<Integer> theses;

    List<Integer> parentCats;

}

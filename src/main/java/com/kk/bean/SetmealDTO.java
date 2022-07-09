package com.kk.bean;


import com.kk.model.entities.Setmeal;
import com.kk.model.entities.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDTO extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

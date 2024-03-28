package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * 套餐业务层
 */
@Service
@Slf4j
@Builder
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(!setmealDishes.isEmpty()){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insert(setmealDishes);
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //根据id查询套餐表的数据
        Setmeal setmeal = setmealMapper.getById(id);
        //复制到VO中
        BeanUtils.copyProperties(setmeal,setmealVO);
        //到分类表中根据id查询名字
        String categoryName = categoryMapper.getBycategoryId(setmeal.getCategoryId());
        setmealVO.setCategoryName(categoryName);
        //到套餐菜品表中找到关系表导入到VO中
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void updateWithSetmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmealDTO.getId();
        if(!setmealDishes.isEmpty()){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            //判断原来关系表是否为空，若为空才删除
            if(!setmealDishMapper.getBySetmealId(setmealId).isEmpty()){
                setmealDishMapper.deleteBySetmealId(setmealId);
            }
            setmealDishMapper.insert(setmealDishes);
        }

    }

    @Override
    public void deleteBetch(List<Long> ids) {
        //查询套餐是否处于启售状态
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //查询是否要菜品绑定了套餐（通过关系表）
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealIds(ids);
        //根据id删除关系表
        if(!setmealDishes.isEmpty()){
            setmealDishMapper.deleteBySetmealIds(ids);
        }
        //删除套餐
        setmealMapper.deleteBetch(ids);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        if (Objects.equals(status,StatusConstant.DISABLE)) {
            List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(id);
            List<Integer> statusList = dishMapper.getStatusByids(dishIds);
            for (Integer statu : statusList) {
                if(Objects.equals(statu,StatusConstant.DISABLE)){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
        List<int[]> ad = new ArrayList<>();
        ad.add(new int[2]);





    }
    //黑马答案代码：
/*    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(Objects.equals(status, StatusConstant.ENABLE)){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && !dishList.isEmpty()){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE.equals(dish.getStatus())){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }*/
}

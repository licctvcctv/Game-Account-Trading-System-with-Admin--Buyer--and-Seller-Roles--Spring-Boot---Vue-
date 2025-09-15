package com.xiaobaitiao.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaobaitiao.springbootinit.model.entity.Commodity;

/**
* @author zhao9
* @description 针对表【commodity】的数据库操作Mapper
* @createDate 2025-03-11 13:35:54
* @Entity generator.domain.Commodity
*/
public interface CommodityMapper extends BaseMapper<Commodity> {
    Commodity selectByIdWithLock(Long id);
}





package com.xiaobaitiao.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaobaitiao.springbootinit.model.entity.CommodityScore;
import org.apache.ibatis.annotations.Select;

/**
* @author zhao9
* @description 针对表【commodity_score】的数据库操作Mapper
* @createDate 2025-03-13 13:48:18
* @Entity generator.domain.CommodityScore
*/
public interface CommodityScoreMapper extends BaseMapper<CommodityScore> {
    /**
     * 查询商品的平均评分
     *
     * @param commodityId 商品 ID
     * @return 平均评分
     */
    @Select("SELECT AVG(score) AS averageScore FROM commodity_score WHERE commodityId = #{commodityId} AND isDelete = 0")
    Double getAverageScoreByCommodityId(Long commodityId);
}





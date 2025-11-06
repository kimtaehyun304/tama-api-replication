package org.example.tamaapi.query.item;

import org.example.tamaapi.domain.item.ColorItemSizeStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ColorItemSizeStockQueryRepository extends JpaRepository<ColorItemSizeStock, Long> {

    // 쇼핑백
    @Query("select isk from ColorItemSizeStock isk join fetch isk.colorItem c join fetch c.color cl join fetch c.item i where isk.id in :ids")
    List<ColorItemSizeStock> findAllWithColorItemAndItemByIdIn(List<Long> ids);


    List<ColorItemSizeStock> findAllByColorItemIdIn(List<Long> colorItemIds);

}

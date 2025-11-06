package org.example.tamaapi.query.item;

import org.example.tamaapi.domain.item.ColorItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ColorItemImageQueryRepository extends JpaRepository<ColorItemImage, Long> {

    List<ColorItemImage> findAllByColorItemId(Long colorItemId);

    List<ColorItemImage> findAllByColorItemIdInAndSequence(List<Long> colorItemIds, Integer sequence);

}

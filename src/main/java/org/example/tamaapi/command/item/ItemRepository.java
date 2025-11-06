package org.example.tamaapi.command.item;

import org.example.tamaapi.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

}

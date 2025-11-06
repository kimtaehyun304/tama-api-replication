package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.item.Color;
import org.example.tamaapi.dto.responseDto.color.ColorResponse;
import org.example.tamaapi.dto.responseDto.color.ParentColorResponse;
import org.example.tamaapi.command.item.ColorRepository;
import org.example.tamaapi.query.item.ColorQueryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
//카테고리 아이템은 itemApi
public class ColorApiController {
    private final ColorQueryRepository colorQueryRepository;

    @GetMapping("/api/colors/parent")
    public List<ParentColorResponse> parentColors() {
        List<Color> colors = colorQueryRepository.findAllByParentIsNull();
        return colors.stream().map(ParentColorResponse::new).toList();
    }

    @GetMapping("/api/colors")
    public List<ColorResponse> colors() {
        List<Color> colors = colorQueryRepository.findAllWithChildrenByParentIsNull();
        return colors.stream().map(ColorResponse::new).toList();
    }

}

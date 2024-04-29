package com.artfriendly.artfriendly.domain.dambyeolag.controller;

import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagDetailsRspDto;

import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagReqDto;
import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagRspDto;
import com.artfriendly.artfriendly.domain.dambyeolag.service.dambyeolag.DambyeolagService;
import com.artfriendly.artfriendly.global.api.RspTemplate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("dambyeolags")
@RequiredArgsConstructor
public class DambyeolagController {
    private final DambyeolagService dambyeolagService;
    @GetMapping
    public RspTemplate<DambyeolagDetailsRspDto> getDambyeolag(@AuthenticationPrincipal long memberId, @NotNull long dambyeolagId) {
        return new RspTemplate<>(HttpStatus.OK,"담벼락 id: "+dambyeolagId+" 세부조회", dambyeolagService.getDetailsDambyeolag(memberId, dambyeolagId));
    }

    @PostMapping
    public RspTemplate<Void> createDambyeolag(@AuthenticationPrincipal long memberId, @Valid @RequestBody DambyeolagReqDto dambyeolagReqDto) {
        dambyeolagService.createDambyeolag(dambyeolagReqDto, memberId);
        return new RspTemplate<>(HttpStatus.CREATED, "담벼락 생성");
    }

    @GetMapping("/lists")
    public RspTemplate<Page<DambyeolagRspDto>> getDambyeolags(@NotNull int page, @NotNull long exhibitionId, String sortType) {
        Page<DambyeolagRspDto> dambyeolagRspDtos = dambyeolagService.getDambyeolagPageOrderBySortType(page, exhibitionId, sortType);
        return new RspTemplate<>(HttpStatus.OK, "담벼락 "+page+"페이지 조회", dambyeolagRspDtos);
    }

    @DeleteMapping
    public RspTemplate<Void> deleteDambyeolag(@AuthenticationPrincipal long memberId, @NotNull long dambyeolagId) {
        dambyeolagService.deleteDambyeolag(memberId, dambyeolagId);
        return new RspTemplate<>(HttpStatus.OK, "담벼락 id: "+dambyeolagId+" 삭제");
    }
    @DeleteMapping("/bookmarks")
    public RspTemplate<Void> deleteBookmark(@AuthenticationPrincipal long memberId, long dambyeolagId) {
        dambyeolagService.deleteBookmark(memberId, dambyeolagId);
        return new RspTemplate<>(HttpStatus.OK, "북마크 해제");
    }

    @PostMapping("/bookmarks")
    public RspTemplate<Void> addBookmark(@AuthenticationPrincipal long memberId, long dambyeolagId) {
        dambyeolagService.addBookmark(memberId, dambyeolagId);
        return new RspTemplate<>(HttpStatus.OK, "북마크 추가");
    }
}

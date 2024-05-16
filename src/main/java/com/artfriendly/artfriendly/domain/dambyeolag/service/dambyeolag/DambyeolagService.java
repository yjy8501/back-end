package com.artfriendly.artfriendly.domain.dambyeolag.service.dambyeolag;

import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagDetailsRspDto;
import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagReqDto;
import com.artfriendly.artfriendly.domain.dambyeolag.dto.dambyeolag.DambyeolagRspDto;
import com.artfriendly.artfriendly.domain.dambyeolag.entity.Dambyeolag;
import com.artfriendly.artfriendly.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface DambyeolagService {
    DambyeolagDetailsRspDto getDetailsDambyeolag(long memberId, long dambyeolagId);
    Page<DambyeolagRspDto> getDambyeolagPageOrderBySortType(int page, long exhibitionId, String sortType);
    void createDambyeolag(DambyeolagReqDto dambyeolagReqDto, long memberId);
    void deleteDambyeolag(long memberId, long dambyeolagId);
    Dambyeolag findById(long dambyeolagId);
    void deleteBookmark(long memberId, long dambyeolagBookmarkId);
    void deleteBookmarksByMember(Member member);
    void addBookmark(long memberId, long dambyeolagId);

}

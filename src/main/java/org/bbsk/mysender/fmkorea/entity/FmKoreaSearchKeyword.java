package org.bbsk.mysender.fmkorea.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FmKoreaSearchKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String useYn;
}
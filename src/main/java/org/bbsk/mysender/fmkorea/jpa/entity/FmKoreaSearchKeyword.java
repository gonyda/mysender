package org.bbsk.mysender.fmkorea.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class FmKoreaSearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String useYn;

}

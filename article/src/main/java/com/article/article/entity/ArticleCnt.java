package com.article.article.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "article_aggr", schema = "public")
public class ArticleCnt implements Serializable, Comparable<ArticleCnt> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @Column(name = "id_seq")
    private int idSeq;

    @Column(name = "article_y")
    private int articleY;

    @Column(name = "article_m")
    private int articleM;

    @Column(name = "article_d")
    private int articleD;

    @Column(name = "article_ymd")
    private LocalDate articleYMD;

    @Column(name = "article_cnt")
    private int articleCnt;

    @Override
    public int compareTo(ArticleCnt other) {
        return this.articleYMD.compareTo(other.articleYMD);
    }

}

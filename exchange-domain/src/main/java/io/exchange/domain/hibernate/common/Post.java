package io.exchange.domain.hibernate.common;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import io.exchange.domain.enums.PostType;
import io.exchange.domain.hibernate.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(Post.Pk.class)
@TableGenerator(name = "post_sequence_generator", 
                table = "sequence",
                pkColumnName = "sequence_name",
                pkColumnValue = "post_sequence",
                valueColumnName = "next_val",
                allocationSize = 1,
                initialValue = 0)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class Post implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long id;
        private PostType type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "post_sequence_generator")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Id
    @Enumerated(EnumType.STRING)
    private PostType type;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    private String title;

    @Lob
    private String content;


    private LocalDateTime modDtm;
    private LocalDateTime delDtm;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public void setModDtm(LocalDateTime localDateTime) {
        this.modDtm = localDateTime;
    }

    public void setDelDtm(LocalDateTime localDateTime) {
        this.delDtm = localDateTime;
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId,PostTypeEnum, RegIp, RegDtm {
        PostBuilder builder = Post.privateBuilder();

        @Override
        public PostTypeEnum userId(Long userId) {
            builder.userId(userId);
            return this;
        }

        @Override
        public RegIp type(PostType type) {
            builder.type(type);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public PostBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        PostTypeEnum userId(Long userId);
    }
    public interface PostTypeEnum {
        RegIp type(PostType type);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        PostBuilder regDtm(LocalDateTime regDtm);
    }
}

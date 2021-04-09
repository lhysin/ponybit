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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import io.exchange.domain.enums.LogTag;
import io.exchange.domain.hibernate.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@TableGenerator(name = "action_log_sequence_generator", 
                table = "sequence",
                pkColumnName = "sequence_name",
                pkColumnValue = "action_log_sequence",
                valueColumnName = "next_val",
                allocationSize = 1,
                initialValue = 0)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class ActionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "action_log_sequence_generator")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private LogTag logTag;

    private String userAgent;

    @Lob
    private String msg;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements LogTagEnum, RegIp, RegDtm {
        ActionLogBuilder builder = ActionLog.privateBuilder();

        @Override
        public RegIp logTag(LogTag logTag) {
            builder.logTag(logTag);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public ActionLogBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static LogTagEnum builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        LogTagEnum userId(Long userId);
    }
    public interface LogTagEnum {
        RegIp logTag(LogTag logTag);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        ActionLogBuilder regDtm(LocalDateTime regDtm);
    }
}

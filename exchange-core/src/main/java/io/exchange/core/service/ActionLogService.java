package io.exchange.core.service;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import eu.bitwalker.useragentutils.UserAgent;
import io.exchange.core.dto.ActionLogDto;
import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.hibernate.repository.ActionLogRepository;
import io.exchange.core.util.CustomPageable;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.LogTag;
import io.exchange.domain.hibernate.common.ActionLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public void log(LogTag logTag) {
        this.log(null, logTag, null);
    }
    public void log(LogTag logTag, String msg) {
        this.log(null, logTag, msg);
    }
    public void log(Long userId, LogTag logTag) {
        this.log(userId, logTag, null);
    }

    public void log(Long userId, LogTag logTag, String msg) {
        try {
            this.createLog(userId, logTag, msg);
        } catch (Exception e) {
            log.debug("actionLogService createLog fail. cause : {}", e);
        }
    }

    @Transactional
    private void createLog(Long userId, LogTag logTag, String msg) {
        String userAgentStr = null;

        try {
            HttpServletRequest request = StaticUtils.getCurrentRequest();
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            if(userAgent != null) {
                userAgentStr = userAgent.getBrowser().getName() + "_" + userAgent.getBrowserVersion();
            }
        } catch (Exception e) {
            log.debug("StaticUtils.getCurrentRequest() is null", e);
        }

        //action logging
        ActionLog actionLog = ActionLog.builder()
                .logTag(logTag)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .userId(userId)
                .userAgent(userAgentStr)
                .msg(msg)
                .build();
        this.actionLogRepository.save(actionLog);
    }

    public ResPage<ActionLogDto.ResActionLog> getActionLogs(ActionLogDto.ReqActionLogs req) {

        Integer page = req.getPage();
        Integer size = req.getSize();
        Long userId = req.getUserId(); 

        Page<ActionLogDto.ResActionLog> results = 
                this.actionLogRepository.findAllByUserIdOrderByRegDtmDesc(userId, CustomPageable.of(page, size));

        CommonDto.ResPage<ActionLogDto.ResActionLog> res = CommonDto.ResPage.<ActionLogDto.ResActionLog>builder()
                .page(page)
                .size(size)
                .list(results.getContent())
                .totalPages(results.getTotalPages())
                .build();

        return res;
    }
}

package com.huansi.dataagent.agent;

import com.huansi.dataagent.agent.dto.AgentRequest;
import com.huansi.dataagent.agent.dto.AgentResponse;
import com.huansi.dataagent.common.ApiException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class DataAgentService {

    private final DataAgentRepository repository;

    public DataAgentService(DataAgentRepository repository) {
        this.repository = repository;
    }

    public List<AgentResponse> list(String keyword, AgentStatus status) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")).stream()
                .filter(agent -> status == null || agent.getStatus() == status)
                .filter(agent -> normalizedKeyword.isEmpty()
                        || agent.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                        || (agent.getDescription() != null
                            && agent.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)))
                .map(AgentResponse::from)
                .toList();
    }

    @Transactional
    public AgentResponse create(AgentRequest request, String owner) {
        DataAgent agent = new DataAgent(
                request.name().trim(),
                normalize(request.description()),
                request.dataSourceType(),
                request.status(),
                owner);
        return AgentResponse.from(repository.save(agent));
    }

    @Transactional
    public AgentResponse update(Long id, AgentRequest request) {
        DataAgent agent = findAgent(id);
        agent.setName(request.name().trim());
        agent.setDescription(normalize(request.description()));
        agent.setDataSourceType(request.dataSourceType());
        agent.setStatus(request.status());
        return AgentResponse.from(repository.save(agent));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "智能体不存在");
        }
        repository.deleteById(id);
    }

    private DataAgent findAgent(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "智能体不存在"));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

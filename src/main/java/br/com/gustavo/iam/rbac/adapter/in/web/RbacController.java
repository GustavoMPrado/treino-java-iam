package br.com.gustavo.iam.rbac.adapter.in.web;

import br.com.gustavo.iam.rbac.adapter.in.web.dto.RoleResponse;
import br.com.gustavo.iam.identidade.domain.Permissao;
import br.com.gustavo.iam.identidade.domain.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// Controller para consultar roles e permissões disponíveis no sistema.
@RestController
public class RbacController {

    @GetMapping("/rbac/roles")
    public List<RoleResponse> listarRoles() {
        List<RoleResponse> roles = new ArrayList<>();

        for (Role role : Role.values()) {
            RoleResponse response = new RoleResponse(
                    role.name(),
                    role.getPermissoes()
            );

            roles.add(response);
        }

        return roles;
    }

    @GetMapping("/rbac/permissoes")
    public List<Permissao> listarPermissoes() {
        return List.of(Permissao.values());
    }
}
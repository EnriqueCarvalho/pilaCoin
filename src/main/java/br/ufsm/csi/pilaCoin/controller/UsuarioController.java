package br.ufsm.csi.pilaCoin.controller;

import br.ufsm.csi.pilaCoin.model.Usuario;
import br.ufsm.csi.pilaCoin.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @CrossOrigin
    @GetMapping("/usuarios")
    public List<Usuario> getUsuarios(){

        try{
            List<Usuario> usuarios = null;
            usuarios = this.usuarioService.getUsuarios();
            return usuarios;
        }catch (Exception e ){
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }

    }
}

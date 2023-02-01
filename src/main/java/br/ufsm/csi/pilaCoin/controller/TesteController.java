package br.ufsm.csi.pilaCoin.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@RestController
public class TesteController {

//    @Autowired
//    private QuadraService quadraService;

    @CrossOrigin
    @GetMapping("/ping")
    public String visualizarQuadra(@ModelAttribute("idUsuario") String idUsuario){

        try{
            return "Vai dar certo";

        }catch (Exception e ){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }



}
package br.ufsm.csi.pilaCoin.controller;

import br.ufsm.csi.pilaCoin.model.PilaCoin;
import br.ufsm.csi.pilaCoin.service.PilaCoinService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PilaCoinController {

    @Autowired
    private PilaCoinService pilaCoinService;

    @SneakyThrows
    @CrossOrigin
    @GetMapping("/buscarPilas")
    private List<PilaCoin> getPilas(){

        List<PilaCoin> pilas = null;
        try{
            pilas = this.pilaCoinService.getPilas();

        }catch (Exception e ){
            e.printStackTrace();
        }
        return pilas;
    }
}

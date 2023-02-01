package br.ufsm.csi.pilaCoin.controller;


import br.ufsm.csi.pilaCoin.service.MineradoraService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MineradoraController {
    @Autowired
    private MineradoraService mineradora;

    @SneakyThrows
    @CrossOrigin
    @PostMapping("/iniciarMineiracao")
    private String iniciarMineiracao(){

        try{
            this.mineradora.mineiracaoAtiva = true;
            new Thread(t1).start();
            new Thread(t1).start();
            return "Mineiração iniciada com sucesso";
        }catch (Exception e ){
            e.printStackTrace();
        }
        return "Erro ao mineirar";
    }


    private Runnable t1 = new Runnable() {


        public void run() {
            try{
               mineradora.minerar();
            } catch (Exception e){}

        }
    };



    @SneakyThrows
    @CrossOrigin
    @PostMapping("/pararMineiracao")
    private String pararMineiracao(){

        try{

            this.mineradora.mineiracaoAtiva = false;
            return "Mineiração parada com sucesso";
        }catch (Exception e ){
            e.printStackTrace();
        }
        return "Erro ao parar de mineirar";
    }



}

package br.ufsm.csi.pilaCoin.controller;

import br.ufsm.csi.pilaCoin.model.Transferencia;
import br.ufsm.csi.pilaCoin.model.Usuario;
import br.ufsm.csi.pilaCoin.service.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TransacaoController {


    @Autowired
    private TransferenciaService transacaoService;

    @CrossOrigin
    @PostMapping("/transferePila")
    public String getUsuarios(@RequestBody Transferencia transferencia){
    String msgResposta = "";
    transferencia.setId(null);
    transferencia.setIdBloco(null);
   

    try{


            try{
                if (transferencia == null){
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Body da request não encontrado"
                    );
                }else{
                    this.transacaoService.tranferePila(transferencia);
                }

                msgResposta = "Pila transferido com sucesso";
            }catch (Exception e ) {
                e.printStackTrace();
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, e.getMessage()
                );
            }
    }

    finally {
        return msgResposta;
    }

    }
}

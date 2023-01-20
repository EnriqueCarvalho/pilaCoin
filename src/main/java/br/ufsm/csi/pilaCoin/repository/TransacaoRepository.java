package br.ufsm.csi.pilaCoin.repository;


import br.ufsm.csi.pilaCoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao,Long> {
}

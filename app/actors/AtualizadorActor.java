package actors;

import java.io.File;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import models.AtualizacaoDAO;
import models.Cidade;
import models.CidadeDAO;
import models.Iniciativa;
import models.IniciativaDAO;
import models.Score;

import org.h2.tools.Csv;

import play.Logger;
import play.db.jpa.JPAApi;
import akka.actor.UntypedActor;

public class AtualizadorActor extends UntypedActor {
	
	@Inject
	private JPAApi jpaAPI;
	
	@Inject
	private AtualizacaoDAO daoAtualizacao;
	
	@Inject
	private CidadeDAO cidadeDAO;
	
	@Inject
	private IniciativaDAO iniciativaDAO;

	private SimpleDateFormat formatoDataAtualizacao = new SimpleDateFormat("yyyyMMdd");
	
	public void onReceive(Object msg) throws Exception {

		Logger.debug("AtualizadorActor.onReceive()");
		if (msg instanceof AtualizadorActorProtocol.AtualizaIniciativasEScores) {
			jpaAPI.withTransaction(() -> daoAtualizacao.inicia());
			
			jpaAPI.withTransaction(() -> {
				try {
					String proxima = daoAtualizacao.find().getProxima();
					Date proximaData = formatoDataAtualizacao.parse(proxima);

					String scoresDataPath = Paths.get(daoAtualizacao.getFolder()).toAbsolutePath().toString() + "/diferentices-" + proxima + ".csv";
					Logger.debug("Usando arquivo de scores: " + scoresDataPath);
					atualizaScores(scoresDataPath, proximaData);
			    	
					String iniciativasDataPath = Paths.get(daoAtualizacao.getFolder()).toAbsolutePath().toString() + "/iniciativas-" + proxima + ".csv";
					Logger.debug("Arquivo de inciativas: " + iniciativasDataPath);
			    	atualizaIniciativas(iniciativasDataPath, proximaData);

			    	daoAtualizacao.finaliza(false);
					sender().tell(true, self());
				} catch (Exception e) {
					e.printStackTrace();
					daoAtualizacao.finaliza(true);
					sender().tell(false, self());
				}
			});
		} else {
			Logger.warn("Mensagem de tipo desconhecido: " + msg.getClass().toString());
		}
	}
	
	private void atualizaScores(String dataPath, Date dataDaAtualizacao) throws SQLException {
    	
    	int count = 0;

    	final ResultSet scoreResultSet = new Csv().read(dataPath, null, "utf-8");
    	count = 0;
    	while (scoreResultSet.next()) {
    		long originID = scoreResultSet.getLong(1);
    		Cidade cidade = cidadeDAO.find(originID);
    		if (cidade == null) {
        		Logger.error("Cidade " + originID + " não encontrada");
        		continue;
    		}

    		Score score = new Score(
    				scoreResultSet.getString(2),
    				scoreResultSet.getFloat(3),
    				scoreResultSet.getFloat(4),
    				scoreResultSet.getFloat(5),
    				scoreResultSet.getFloat(6));
    		
    		cidade.atualizaScore(score, dataDaAtualizacao);
    		
    		cidadeDAO.save(cidade);
    		
    		count++;
    		if (count % 2000 == 0) {
    			Logger.info("Atualizou " + count + " scores nas cidades.");
    		}
    	}
    	scoreResultSet.close();
    	
    	new File(dataPath).delete();
	}
	
    	


	
	private void atualizaIniciativas(String dataPath, Date dataDaAtualizacao) throws SQLException {

		ResultSet resultSet = new Csv().read(dataPath, null, "utf-8");
		int count = 0;
		while (resultSet.next()) {

			long idIniciativa = resultSet.getLong("NR_CONVENIO");

			Cidade cidadeDaIniciativa = cidadeDAO.find(resultSet.getLong("cod7"));
			if (cidadeDaIniciativa == null) {
				Logger.error("Cidade " + resultSet.getLong("cod7") + " não encontrada para iniciativa " + idIniciativa);
				continue;
			}

			Iniciativa iniciativaAtualizada = parseIniciativa(resultSet);
			if(iniciativaAtualizada == null){
				continue;
			}
			
			Iniciativa iniciativa = iniciativaDAO.find(idIniciativa);
			if (iniciativa == null) {
				cidadeDaIniciativa.addIniciativa(iniciativaAtualizada, dataDaAtualizacao);
				cidadeDAO.save(cidadeDaIniciativa);
			}else{
				iniciativa.atualiza(iniciativaAtualizada, dataDaAtualizacao);
				iniciativaDAO.save(iniciativa);
			}

			count++;
			if (count % 2000 == 0) {
				Logger.info("Atualizou " + count + " iniciativas.");
			}
		}
		
		new File(dataPath).delete();
    }

	private Iniciativa parseIniciativa(ResultSet resultSet) {
		try{
			float verbaGovernoFederal = resultSet.getString("VL_REPASSE_CONV").contains("NA") ? 0f : resultSet.getFloat("VL_REPASSE_CONV"); // repasse
			float verbaMunicipio = resultSet.getString("VL_CONTRAPARTIDA_CONV").contains("NA") ? 0f : resultSet.getFloat("VL_CONTRAPARTIDA_CONV");    // contrapartida

			DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
			Date dataConclusao = formatter.parse(resultSet.getString("DIA_FIM_VIGENC_CONV"));

			// Adicionando 2 meses para o prazo de prestação de contas
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(dataConclusao);
			cal.add(GregorianCalendar.MONTH, 2);
			Date dataConclusaoGovernoFederal = cal.getTime();


			return new Iniciativa(
					resultSet.getLong("NR_CONVENIO"),        // id
					resultSet.getInt("ANO"),        // ano
					resultSet.getString("OBJETO_PROPOSTA"),    // titulo
					resultSet.getString("Nome Programa"),    // programa
					resultSet.getString("funcao.imputada"),    // area
					resultSet.getString("DESC_ORGAO_SUP"),        // fonte
					resultSet.getString("DESC_ORGAO"),    // concedente
					resultSet.getString("TX_STATUS"),    // status
					false,//resultSet.getBoolean(50),    // temAditivo
					verbaGovernoFederal,        // verba do governo federal
					verbaMunicipio,                // verba do municipio
					formatter.parse(resultSet.getString("DIA_INIC_VIGENC_CONV")),        // data de inicio
					dataConclusao,    // data de conclusao municipio
					dataConclusaoGovernoFederal);	
		}catch(SQLException | ParseException e){
			Logger.error("Erro no parsing da iniciativa em: " + resultSet.toString());
		}
		return null;
	}

}

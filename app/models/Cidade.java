package models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Cidade implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 3104811717507160248L;
	public static final String TABLE = "Cidade";
	@Id
	private Long id;
    private String nome;
    private String uf;
    private Float idhm;
    private Float idhmRenda;
    private Float idhmLongevidade;
    private Float idhmEducacao;
    private Long populacao;

    @ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cidades_similares", 
	joinColumns = { @JoinColumn(name = "origin_id", referencedColumnName = "id")  }, 
	inverseJoinColumns = { @JoinColumn(name = "similar_id", referencedColumnName = "id") })
    @JsonBackReference
    private List<Cidade> similares;
    
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Score> scores;

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Iniciativa> convenios;

    public Cidade() {
        this.similares = new LinkedList<>();
        this.scores = new LinkedList<>();
        this.convenios = new LinkedList<>();
	}

	public Cidade(Long id, String nome, String uf, Float idhm, Float idhmRenda,
			Float idhmLongevidade, Float idhmEducacao, Long populacao) {
		this();
		this.id = id;
		this.nome = nome;
		this.uf = uf;
		this.idhm = idhm;
		this.idhmRenda = idhmRenda;
		this.idhmLongevidade = idhmLongevidade;
		this.idhmEducacao = idhmEducacao;
		this.populacao = populacao;
	}

	public Cidade(String nome) {
        this.nome = nome;
        this.similares = new LinkedList<Cidade>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public Float getIdhm() {
		return idhm;
	}

	public void setIdhm(Float idhm) {
		this.idhm = idhm;
	}

	public Float getIdhmRenda() {
		return idhmRenda;
	}

	public void setIdhmRenda(Float idhmRenda) {
		this.idhmRenda = idhmRenda;
	}

	public Float getIdhmLongevidade() {
		return idhmLongevidade;
	}

	public void setIdhmLongevidade(Float idhmLongevidade) {
		this.idhmLongevidade = idhmLongevidade;
	}

	public Float getIdhmEducacao() {
		return idhmEducacao;
	}

	public void setIdhmEducacao(Float idhmEducacao) {
		this.idhmEducacao = idhmEducacao;
	}

	public Long getPopulacao() {
		return populacao;
	}

	public void setPopulacao(Long populacao) {
		this.populacao = populacao;
	}

	public List<Cidade> getSimilares() {
        return similares;
    }

    public void setSimilares(List<Cidade> similares) {
        this.similares = similares;
    }

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}
	
    @JsonIgnore
    public List<Iniciativa> getConvenios() {
        return convenios;
    }

    public void setConvenios(List<Iniciativa> convenios) {
        this.convenios = convenios;
    }


	@Override
	public String toString() {
		return "Cidade [id=" + id + ", nome=" + nome + ", uf=" + uf + ", idhm="
				+ idhm + ", idhmRenda=" + idhmRenda + ", idhmLongevidade="
				+ idhmLongevidade + ", idhmEducacao=" + idhmEducacao
				+ ", populacao=" + populacao + ", similares=" + similares.size() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cidade other = (Cidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}

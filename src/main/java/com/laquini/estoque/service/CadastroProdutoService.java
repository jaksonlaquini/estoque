package com.laquini.estoque.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.laquini.estoque.model.Produto;
import com.laquini.estoque.repository.Produtos;
import com.laquini.estoque.repository.filter.ProdutoFilter;

@Service
public class CadastroProdutoService {

	@Autowired
	private Produtos produtos;
	
	public void salvar(Produto produto) {
		try {
			produtos.save(produto);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException("Formato de data inválido");
		}
	}

	public void excluir(Long codigo) {
		produtos.delete(codigo);
	}

//	public String receber(Long codigo) {
//		Produto produto = produtos.findOne(codigo);
//		produto.setStatus(StatusTitulo.RECEBIDO);
//		produtos.save(produto);
//		
//		return StatusTitulo.RECEBIDO.getDescricao();
//	}
	
	public void entradaEstoque(Long codigo, Long quantidade) {
		Produto produto = produtos.findOne(codigo);
		produto.setQtdEstoque((int) (produto.getQtdEstoque() + quantidade));
		produtos.save(produto);

	}
	
	public void baixaEstoque(Long codigo, Long quantidade) {
		Produto produto = produtos.findOne(codigo);
		produto.setQtdEstoque((int) (produto.getQtdEstoque() - quantidade));
		produtos.save(produto);

	}
	
	public List<Produto> filtrar(ProdutoFilter filtro) {
		String descricao = filtro.getDescricao() == null ? "%" : filtro.getDescricao();
		return produtos.findByDescricaoContaining(descricao);
	}
	
}

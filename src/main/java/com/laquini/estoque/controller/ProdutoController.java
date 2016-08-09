package com.laquini.estoque.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laquini.estoque.model.Produto;
import com.laquini.estoque.model.StatusTitulo;
import com.laquini.estoque.repository.filter.ProdutoFilter;
import com.laquini.estoque.service.CadastroProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
	
	private static final String CADASTRO_VIEW = "CadastroProduto";
	private static final String PESQUISA_PRODUTO_VIEW = "PesquisaProduto";
	
	@Autowired
	private CadastroProdutoService cadastroProdutoService;

	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(new Produto());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Produto produto, Errors errors, RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			return CADASTRO_VIEW;
		}
		
		try {
			produto.setDataInsercao(new Date());
			cadastroProdutoService.salvar(produto);
			attributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
			return "redirect:/produtos/novo";
		} catch (IllegalArgumentException e) {
			errors.rejectValue("descricao", null, e.getMessage());
			return CADASTRO_VIEW;
		}
	}
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") ProdutoFilter filtro) {
		List<Produto> todosTitulos = cadastroProdutoService.filtrar(filtro);
		
		ModelAndView mv = new ModelAndView("PesquisaProduto");
		mv.addObject("produtos", todosTitulos);
		return mv;
	}
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Produto produto) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW); 
		mv.addObject(produto);
		return mv;
	}
	
	@RequestMapping(value="{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes) {
		cadastroProdutoService.excluir(codigo);
		attributes.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
		return "redirect:/produtos";
	}
	
//	@RequestMapping(value = "/{codigo}/receber", method = RequestMethod.PUT)
//	public @ResponseBody String receber(@PathVariable Long codigo) {
//		return cadastroTituloService.receber(codigo);
//	}
	
	@RequestMapping(value = "{codigo}/{txtInput}/{operacao}", method = RequestMethod.PUT)
	public String entradaEstoque(@PathVariable Long codigo, @PathVariable Long txtInput, @PathVariable Long operacao, RedirectAttributes attributes) {
		try {
			if(operacao == 1){
				cadastroProdutoService.entradaEstoque(codigo, txtInput);
			}else {
				
				cadastroProdutoService.baixaEstoque(codigo, txtInput);
			}
			
			attributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
			return "redirect:/produtos";
		} catch (IllegalArgumentException e) {
//			errors.rejectValue("descricao", null, e.getMessage());
			return PESQUISA_PRODUTO_VIEW;
		}
		
//		return null;
	}
	
	@ModelAttribute("todosStatusTitulo")
	public List<StatusTitulo> todosStatusTitulo() {
		return Arrays.asList(StatusTitulo.values());
	}
	
}

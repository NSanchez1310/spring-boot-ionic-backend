package com.nsanchez1310.cursomc;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nsanchez1310.cursomc.domain.Categoria;
import com.nsanchez1310.cursomc.domain.Cidade;
import com.nsanchez1310.cursomc.domain.Cliente;
import com.nsanchez1310.cursomc.domain.Endereco;
import com.nsanchez1310.cursomc.domain.Estado;
import com.nsanchez1310.cursomc.domain.ItemPedido;
import com.nsanchez1310.cursomc.domain.Pagamento;
import com.nsanchez1310.cursomc.domain.PagamentoComBoleto;
import com.nsanchez1310.cursomc.domain.PagamentoComCartao;
import com.nsanchez1310.cursomc.domain.Pedido;
import com.nsanchez1310.cursomc.domain.Produto;
import com.nsanchez1310.cursomc.domain.enums.EstadoPagamento;
import com.nsanchez1310.cursomc.domain.enums.TipoCliente;
import com.nsanchez1310.cursomc.repositories.CategoriaRepository;
import com.nsanchez1310.cursomc.repositories.CidadeRepository;
import com.nsanchez1310.cursomc.repositories.ClienteRepository;
import com.nsanchez1310.cursomc.repositories.EnderecoRepository;
import com.nsanchez1310.cursomc.repositories.EstadoRepository;
import com.nsanchez1310.cursomc.repositories.ItemPedidoRepository;
import com.nsanchez1310.cursomc.repositories.PagamentoRepository;
import com.nsanchez1310.cursomc.repositories.PedidoRepository;
import com.nsanchez1310.cursomc.repositories.ProdutoRepository;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/*
		 * Instancia os objetos preparando para gravação no BD
		 */
		
		Categoria cat1 = new Categoria(null, "Informatica");
		Categoria cat2 = new Categoria(null, "Escritorio");

		Produto p1 = new Produto(null, "Computador", 2000.00);
		Produto p2 = new Produto(null, "Impressora", 800.00);
		Produto p3 = new Produto(null, "Mouse", 80.00);
		
		/*
		 * Instancia a lista, montando o relacionamento many to many das tabelas
		 */
		// 1) Relaciona categorias com produtos
		cat1.getProdutos().addAll(Arrays.asList(p1, p2, p3));
		cat2.getProdutos().addAll(Arrays.asList(p2));
	
		// 2) Relaciona categoria produtos com categorias
		p1.getCategorias().addAll(Arrays.asList(cat1));
		p2.getCategorias().addAll(Arrays.asList(cat1, cat2));	
		p3.getCategorias().addAll(Arrays.asList(cat1));	

		
		/* Grava no DB
		 */
		categoriaRepository.saveAll(Arrays.asList(cat1, cat2));
		produtoRepository.saveAll(Arrays.asList(p1, p2, p3));
		
		// 3) Relaciona Estado e Cidade
		Estado est1 = new Estado(null, "Minas Gerais");
		Estado est2 = new Estado(null, "São Paulo");
		
		Cidade c1 = new Cidade(null, "Uberlândia", est1);
		Cidade c2 = new Cidade(null, "São Paulo", est2);
		Cidade c3 = new Cidade(null, "Campinas", est2);
		
		est1.getCidades().addAll(Arrays.asList(c1));
		est2.getCidades().addAll(Arrays.asList(c2, c3));
		
		estadoRepository.saveAll(Arrays.asList(est1, est2));
		cidadeRepository.saveAll(Arrays.asList(c1, c2, c3));
		
		Cliente cli1 = new Cliente(null, "Maria da Silva", "maria@gmail.com", "3365136789", TipoCliente.PESSOAFISICA);

		cli1.getTelefones().addAll(Arrays.asList("27365631", "904567382"));
		
		Endereco e1 = new Endereco(null, "Rua Flores", "300", "Apto 303", "Jardim Guedala", "21987654", cli1, c1);
		Endereco e2 = new Endereco(null, "Av Matos", "105", "Sala 800", "Centro", "38765123", cli1, c2);
		
		cli1.getEnderecos().addAll(Arrays.asList(e1, e2));
		
		clienteRepository.saveAll(Arrays.asList(cli1));
		enderecoRepository.saveAll(Arrays.asList(e1, e2));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		Pedido ped1 = new Pedido(null, sdf.parse("30/09/2019 10:32"), cli1, e1 );
		Pedido ped2 = new Pedido(null, sdf.parse("10/10/2019 19:34"), cli1, e2 );
		
		Pagamento pagto1 = new PagamentoComCartao(null, EstadoPagamento.QUITADO, ped1, 6);
		ped1.setPagamento(pagto1);
		
		Pagamento pagto2 = new PagamentoComBoleto(null, EstadoPagamento.PENDENTE, ped2, sdf.parse("20/10/2019 00:00"), null);
		ped2.setPagamento(pagto2);
		
		cli1.getPedidos().addAll(Arrays.asList(ped1, ped2));
		
		pedidoRepository.saveAll(Arrays.asList(ped1, ped2));
		pagamentoRepository.saveAll(Arrays.asList(pagto1, pagto2));
		
		ItemPedido ip1 = new ItemPedido(ped1, p1, 0.00, 1, 2000.00);
		ItemPedido ip2 = new ItemPedido(ped1, p3, 0.00, 2, 80.00);
		ItemPedido ip3 = new ItemPedido(ped2, p2, 100.00, 1, 800.00);
		
		ped1.getItens().addAll(Arrays.asList(ip1, ip2));
		ped2.getItens().addAll(Arrays.asList(ip3));
		
		p1.getItens().addAll(Arrays.asList(ip1));
		p2.getItens().addAll(Arrays.asList(ip3));
		p3.getItens().addAll(Arrays.asList(ip2));
		
		itemPedidoRepository.saveAll(Arrays.asList(ip1, ip2, ip3));		
		
	}
	

}

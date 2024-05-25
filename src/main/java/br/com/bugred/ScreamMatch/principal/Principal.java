package br.com.bugred.ScreamMatch.principal;

import br.com.bugred.ScreamMatch.model.DadosSerie;
import br.com.bugred.ScreamMatch.model.DadosTemporada;
import br.com.bugred.ScreamMatch.model.Episodio;
import br.com.bugred.ScreamMatch.service.ConsumoApi;
import br.com.bugred.ScreamMatch.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner sc = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=48b676d5";

    public void exibeMenu() {
        System.out.println("Digite o nome da séria para buscar: ");
        String nomeSerie = sc.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> listaTemporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season="+ i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }

        listaTemporadas.forEach(System.out::println);

//        listaTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
//
//        List<DadosEpisodio> dadosEpisodios = listaTemporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());

//        System.out.println("\nTop 5 episodios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e-> System.out.println("Primeiro filtro(N/A): " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e-> System.out.println("Ordenação: " + e))
//                .limit(5)
//                .peek(e-> System.out.println("Limit: " + e))
//                .forEach(System.out::println);

        List<Episodio> listEpisodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

//        listEpisodios.forEach(System.out::println);
//        System.out.println("Digite um trecho do titulo de um episodio: ");
//        String trechoTitulo = sc.nextLine();
//
//        Optional<Episodio> ep = listEpisodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (ep.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: " + ep.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }

//        System.out.println("A partir de que ano deseja ver os episodios: ");
//        var ano = sc.nextInt();
//        sc.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        listEpisodios.stream()
//                .filter(e-> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data Lançamento: " + e.getDataLancamento().format(formatador)
//                ));


        //Tranformando uma lista de coisas num Map
        Map<Integer, Double> rankinPorTemporada = listEpisodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(rankinPorTemporada);

        DoubleSummaryStatistics est = listEpisodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.printf("Média: %.2f\n", est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}

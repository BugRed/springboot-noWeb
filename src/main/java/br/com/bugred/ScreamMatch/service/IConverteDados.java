package br.com.bugred.ScreamMatch.service;

public interface IConverteDados {
    <T> T obterDados(String json,Class<T> classe);
}

package com.example.reflejos;

class ModeloLista {

    private int imagen;
    private String titulo;
    private String texto1;

    private String texto2;

    public ModeloLista() {
    }

    public ModeloLista(int idImagen, String textoTitulo, String texto1, String texto2) {
        this.imagen = idImagen;
        this.titulo = textoTitulo;
        this.texto1 = texto1;
        this.texto2 = texto2;
    }

    public int get_idImagen() {
        return imagen;
    }

    public String get_textoTitulo() {
        return titulo;
    }

    public String get_texto1() {
        return texto1;
    }

    public String get_texto2() {
        return texto2;
    }
}

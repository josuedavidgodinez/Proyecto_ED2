package com.example.godin.proyecto_ed2;

public class Conversacion {

    private mensaje mensajes[];
    private String usuario1;
    private  String usuario2;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public mensaje[] getMensajes() {
        return mensajes;
    }

    public void setMensajes(mensaje[] mensajes) {
        this.mensajes = mensajes;
    }

    public String getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    public String getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(String usuario2) {
        this.usuario2 = usuario2;
    }
}

import greenfoot.*;

public class Mundo00 extends MundoBase {

    public Mundo00() {
        super(6, 5);
    }

    protected void generarNaves() {
        agregar(new NaveExploradora(), 1, 0);
        agregar(new NaveDeAtaque(), 2, 1);
        agregar(new NaveRecolectora(), 3, 2);

        agregar(new NaveExploradoraEnemiga(Direccion.NORTE), 4, 0);
        agregar(new NaveDeAtaqueEnemiga(Direccion.NORTE), 5, 1);
        
        NaveDeAtaque nave = new NaveDeAtaque();
        agregar(nave, 3, 4);

        PilotoRescate piloto = new PilotoRescate();
        agregar(piloto, 0, 4);
        piloto.subirse(nave);
    }

    protected void generarPOIs() {
        marcarCelda(0, 0, new Color(0, 0, 200, 150));
        marcarCelda(5, 0, new Color(200, 0, 0, 150));
    }

    protected void generarItems() {
        agregar(new ItemDeCombustible(), 0, 1);
        agregar(new ItemDeCombustible(), 1, 3);
        agregar(new ContenedorDeRecursos(500, "MAT-001"), 2, 0);
        agregar(new ContenedorDeRecursos(300, "OMEGA-002"), 3, 3);
    }

    protected void generarAsteroides() {
        agregar(new Asteroide(), 0, 2);
        agregar(new AsteroideDeMineral(), 0, 3);
    }
}

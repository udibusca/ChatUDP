package chat.udp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServidorUDP extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	static int conecxoes = 0;

	static int maximoConecxoesPermitidas = 10;

	static JTextField mensagem = new JTextField("");
	static JTextField mensagemEdit = new JTextField("");
	private JScrollPane scrollpanel;
	static JTextArea textarea;
	JButton sair = new JButton("Sair");
	private static byte[] buffer = null;

	private static DatagramSocket socketServidor = null;
	private static MulticastSocket socketCliente = null;
	private static DatagramPacket pacotesDeTransicao = null;

	private static final String GRUPO = "224.0.0.25";
	private static final int PORTA_SERVIDOR = 60005;
	public static final int PORTA_GRUPO = 60006;

	public static InetAddress gerenciarGrupo;
	//TODO Futuramente listar os clientes conectados
	//private static ArrayList<ClienteUDP> clientes = new ArrayList<>();

	/**
	 * Construtor
	 */
	// Inicio constructor
	// ------------------------------------------------------------------
	public ServidorUDP() {
		super("Servidor chat");

		setLayout(null);

		mensagem.setBounds(10, 10, 400, 30);
		add(mensagem);
		mensagem.setEditable(false);

		mensagemEdit.setBounds(10, 348, 400, 30);
		add(mensagemEdit);
		mensagem.setEditable(false);

		textarea = new JTextArea();
		scrollpanel = new JScrollPane(textarea);
		scrollpanel.setBounds(10, 50, 400, 300);
		add(scrollpanel);

		sair.setBounds(420, 10, 100, 30);
		add(sair);

		textarea.setEditable(false);
		sair.addActionListener(this);
		/**
		 * O fechamento da janela é cancelado para que a finalização do servidor seja
		 * feita a partir do botão Sair
		 */
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	}
	// Fim constructor
	// ------------------------------------------------------------------

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sair) {
			if (socketServidor != null) {
				socketServidor.close();
			}
			if (socketCliente != null) {
				socketCliente.close();
			}

			System.exit(0);
		}
	}

	public static void main(String args[]) throws IOException {

		System.out.println("Servidor iniciado.");

		ServidorUDP pantalla = new ServidorUDP();
		pantalla.setBounds(0, 0, 540, 400);
		pantalla.setVisible(true);
		mensagem.setText("\r\n" + "Número de conexões atuais: " + conecxoes);

		socketServidor = new DatagramSocket(PORTA_SERVIDOR);
		gerenciarGrupo = InetAddress.getByName(GRUPO);

		String mensagem;

		while (true) {

			buffer = new byte[1024];
			pacotesDeTransicao = new DatagramPacket(buffer, buffer.length);
			System.out.println("---- Ok");
			socketServidor.receive(pacotesDeTransicao);

			System.out.println("servidor recebe: " + new String(pacotesDeTransicao.getData()));
			mensagem = new String(pacotesDeTransicao.getData());
			textarea.append(mensagem + "\n");

			System.out.println(mensagem);
			pacotesDeTransicao = new DatagramPacket(mensagem.getBytes(), mensagem.length(), gerenciarGrupo,
					PORTA_GRUPO);
			socketServidor.send(pacotesDeTransicao);
			System.out.println("Enviado a clientes: " + new String(pacotesDeTransicao.getData()));

		}

	}

}

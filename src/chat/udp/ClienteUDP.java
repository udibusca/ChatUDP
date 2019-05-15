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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteUDP extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	String nomeUsuario;
	static JTextField mensagem = new JTextField();
	private JScrollPane scrollpanel;
	static JTextArea textarea1;
	JButton btnEnviar = new JButton("Enviar");
	JButton btnDesconectar = new JButton("Sair");

	private static final String GRUPO = "224.0.0.25";
	private static final int SERVIDOR_PORTA = 60005;
	private static final int GRUPO_PORTA = 60006;
	private static final String SERVIDOR = "localhost";
	private static boolean repetir = true;

	public static InetAddress gerenciarGrupo = null;
	public static InetAddress gerenciarServer = null;

	private static MulticastSocket multiSocket = null;
	public static DatagramSocket socket = null;

	private static byte[] buffer = new byte[1024];

	public ClienteUDP(String nome) {
		super("Conexão do cliente chat tcp");

		this.nomeUsuario = nome;
		System.out.println("O nome é : " + this.nomeUsuario);
		setLayout(null);
		mensagem.setBounds(10, 10, 400, 30);
		add(mensagem);
		textarea1 = new JTextArea();
		scrollpanel = new JScrollPane(textarea1);
		scrollpanel.setBounds(10, 50, 400, 300);
		add(scrollpanel);
		btnEnviar.setBounds(420, 10, 100, 30);
		add(btnEnviar);
		btnDesconectar.setBounds(420, 50, 100, 30);
		add(btnDesconectar);
		textarea1.setEditable(false);
		btnEnviar.addActionListener(this);
		btnDesconectar.addActionListener(this);

		// O fechamento da janela é cancelado para forçar o botão a sair
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		System.out.println("FIM CONSTRUTOR");

	}

	public String getNome() {
		return this.nomeUsuario;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btnDesconectar) {

			String texto = ">> " + nomeUsuario + " Saiu do chat!!!";
			DatagramPacket paquete = new DatagramPacket(texto.getBytes(), texto.length(), gerenciarServer,
					SERVIDOR_PORTA);
			try {
				socket.send(paquete);
				socket.close();
				repetir = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.exit(0);
		}
		if (ae.getSource() == btnEnviar) {
			System.out.println("Enviando mensagem para o chat UDP");
			String texto = nomeUsuario + " Diz : " + mensagem.getText().toString();

			DatagramPacket paquete = new DatagramPacket(texto.getBytes(), texto.length(), gerenciarServer,
					SERVIDOR_PORTA);
			try {

				socket.send(paquete);

			} catch (IOException e) {
				e.printStackTrace();
			}
			mensagem.setText("");
		}
	}

	public static void main(String[] args) throws Exception {

		/**
		 * PopUp de colocar o nome do usuario.
		 */
		String nomeUser = JOptionPane.showInputDialog("Entre com o seu nome de usuário");
		DatagramPacket pacote = null;

		if (!nomeUser.trim().equals("")) {

			ClienteUDP cliente = new ClienteUDP(nomeUser);

			System.out.println("Seu Nome : " + cliente.getNome());

			cliente.setBounds(0, 0, 540, 400);
			cliente.setVisible(true);

			try {

				socket = new DatagramSocket();
				multiSocket = new MulticastSocket(GRUPO_PORTA);
				gerenciarGrupo = InetAddress.getByName(GRUPO);
				gerenciarServer = InetAddress.getByName(SERVIDOR);
				multiSocket.joinGroup(gerenciarGrupo);

				pacote = new DatagramPacket(nomeUser.getBytes(), nomeUser.length(), gerenciarServer, SERVIDOR_PORTA);
				socket.send(pacote);
				System.out.println("primeiro envio");

				while (repetir) {

					buffer = new byte[1024];
					pacote = new DatagramPacket(buffer, buffer.length);
					multiSocket.receive(pacote);

					System.out.println("o cliente recebe o pacote pelo multi-socket: " + new String(pacote.getData()));

					String texto = new String(pacote.getData());
					textarea1.append(texto + "\n");

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Favor informar um nome.");
		}
	}

}

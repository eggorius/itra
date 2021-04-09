import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class Main {
  public static String computerMove(byte[] seed, String[] args, int move) {
    HMac hmac = new HMac(new SHA256Digest());
    hmac.init(new KeyParameter(seed));
    byte[] bytes = args[move].getBytes(StandardCharsets.UTF_8);
    hmac.update(bytes, 0, bytes.length);
    byte[] result = new byte[hmac.getMacSize()];
    hmac.doFinal(result, 0);
    return Hex.toHexString(result).toUpperCase();
  }


  public static void main(String[] args) {
    Set<String> items = new HashSet<>(Arrays.asList(args));
    if (args.length < 3 || args.length % 2 == 0 || args.length != items.size()) {
      System.out.println("The wrong input. Try this: rock scissors paper");
      return;
    }
    SecureRandom secureRandom = new SecureRandom();

    Scanner sc = new Scanner(System.in);
    while (true) {
      byte[] hmacKey = secureRandom.generateSeed(16);
      int compMove = secureRandom.nextInt(args.length);

      System.out.println("HMAC: " + computerMove(hmacKey, args, compMove));

      for (int i = 0; i < args.length; i++) {
        System.out.println((i + 1) + " - " + args[i]);
      }
      System.out.println("0 - exit");
      System.out.print("Enter your move: ");
      int move = sc.nextInt();

      if (move == 0) return;
      if (move < 1 || move > args.length) continue;

      System.out.println("Your move: " + args[move - 1]);
      System.out.println("Computer move: " + args[compMove]);

      if (args[move - 1].equals(args[compMove])) {
        System.out.println("Tie!");
      } else {
        boolean won = true;
        for (int i = 1; i <= args.length / 2; i++) {
          if (move == (compMove + i) % args.length + 1) {
            won = false;
            break;
          }
        }
        if (won) {
          System.out.println("You won!");
        } else {
          System.out.println("You lost!");
        }
      }
      System.out.println("HMAC key: " + Hex.toHexString(hmacKey).toUpperCase());
    }
  }
}

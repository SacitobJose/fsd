import java.util.ArrayList;
import java.util.Random;

import io.atomix.cluster.messaging.ManagedMessagingService;
import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;
import io.atomix.utils.serializer.Serializer;
import io.atomix.utils.serializer.SerializerBuilder;

/**
 * Client
 */
public class Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Indique o endereço do cliente e os dos servidores (ip:porta)");
            System.exit(1);
        }

        Address myAddress = Address.from(args[0]);
        ArrayList<Address> servers = new ArrayList<Address>(args.length - 1);
        for (int i = 1; i < args.length; i++)
            servers.add(Address.from(args[i]));

        ManagedMessagingService ms = new NettyMessagingService("twitter", myAddress, new MessagingConfig());
        ms.start();

        Serializer tweetSerializer = new SerializerBuilder().addType(Tweet.class).build();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            ArrayList<String> topics = new ArrayList<>();
            topics.add("#f" + random.nextInt());
            topics.add("#t" + random.nextInt());
            Tweet t = new Tweet("User", String.valueOf(random.nextInt()), topics);
            ms.sendAsync(servers.get(random.nextInt(servers.size())), "publishTweet", tweetSerializer.encode(t));
        }
    }
}

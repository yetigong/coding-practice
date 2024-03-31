package sigma.orders;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

public class ClientOrders {
    static class Client {
        int clientId;
        String name;
        String state;

        public Client(int clientId, String name, String state) {
            this.clientId = clientId;
            this.name = name;
            this.state = state;
        }

        @Override
        public String toString() {
            return String.format("[Client id: %s, name: %s, state: %s]", this.clientId, this.name, this.state);
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.clientId) + name.hashCode() + state.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(Client.class)) {
                return false;
            }

            Client that = (Client) obj;
            return this.clientId == that.clientId && this.name
                    .equals(that.name) && this.state.equals(that.state);
        }
    }


    static class Order {
        int orderId;
        int clientId;
        String date;
        Double total;

        public Order(int orderId, int clientId, String date, Double total) {
            this.orderId = orderId;
            this.clientId = clientId;
            this.date = date;
            this.total = total;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "orderId=" + orderId +
                    ", clientId=" + clientId +
                    ", date='" + date + '\'' +
                    ", total=" + total +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Order order = (Order) o;
            return orderId == order.orderId && clientId == order.clientId && Objects.equals(date, order.date) && Objects.equals(total, order.total);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId, clientId, date, total);
        }
    }

    static class ClientOrder {
        int orderId;
        Integer clientId;
        String date;
        Double total;
        String clientName;
        String clientState;

        public ClientOrder(int orderId, Integer clientId, String date, Double total, String clientName, String clientState) {
            this.orderId = orderId;
            this.clientId = clientId;
            this.date = date;
            this.total = total;
            this.clientName = clientName;
            this.clientState = clientState;
        }

        @Override
        public String toString() {
            return "ClientOrder{" +
                    "orderId=" + orderId +
                    ", clientId=" + clientId +
                    ", date='" + date + '\'' +
                    ", total=" + total +
                    ", clientName='" + clientName + '\'' +
                    ", clientState='" + clientState + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientOrder that = (ClientOrder) o;
            return orderId == that.orderId && Objects.equals(this.clientId, that.clientId)
                    && Objects.equals(date, that.date) && Objects.equals(total, that.total)
                    && Objects.equals(clientName, that.clientName) && Objects.equals(clientState, that.clientState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId, clientId, date, total, clientName, clientState);
        }
    }

    static class ClientSum {
        int clientId;
        String clientName;

        double sum;

        public ClientSum(int clientId, String clientName, double sum) {
            this.clientId = clientId;
            this.clientName = clientName;
            this.sum = sum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientSum clientSum = (ClientSum) o;
            return clientId == clientSum.clientId && Double.compare(sum, clientSum.sum) == 0 && Objects.equals(clientName, clientSum.clientName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, clientName, sum);
        }

        @Override
        public String toString() {
            return "ClientSum{" +
                    "clientId=" + clientId +
                    ", clientName='" + clientName + '\'' +
                    ", sum=" + sum +
                    '}';
        }
    }

    // need to clarify join type
    public static List<ClientOrder> leftOuterJoin(List<Order> orders, List<Client> clients) {
        Map<Integer, Client> clientsMap = clients.stream()
                .collect(Collectors.toMap(client -> client.clientId, client -> client));

        return orders.stream().map(order -> {
            if (clientsMap.containsKey(order.clientId)) {
                Client client = clientsMap.get(order.clientId);
                return new ClientOrder(order.orderId, order.clientId, order.date, order.total, client.name, client.state);
            } else {
                return new ClientOrder(order.orderId, order.clientId, order.date, order.total, null, null);
            }
        }).collect(Collectors.toList());
    }

    @Test
    public void testLeftOuterJoin() {
        List<Order> orders = List.of(
                new Order(1000, 2000, "2024-03-08", 100.00),
                new Order(1001, 2000, "2024-03-08", 200.00),
                new Order(1002, 2001, "2024-03-08", 300.00),
                new Order(1003, 3000, "2024-03-08", 400.00)
        );

        List<Client> clients = List.of(
                new Client(2000, "Bo", "CA"),
                new Client(2001, "Anna", "CA"),
                new Client(2002, "Cat", "CA")
        );

        List<ClientOrder> clientOrders = List.of(
                new ClientOrder(1000, 2000, "2024-03-08", 100.00, "Bo", "CA"),
                new ClientOrder(1001, 2000, "2024-03-08", 200.00, "Bo", "CA"),
                new ClientOrder(1002, 2001, "2024-03-08", 300.00, "Anna", "CA"),
                new ClientOrder(1003, 3000, "2024-03-08", 400.00, null, null)
        );

        List<ClientOrder> joint = leftOuterJoin(orders, clients);

        Assert.assertEquals(joint, clientOrders);
    }

    public static List<ClientSum> groupByClients(List<Order> orders, List<Client> clients, int limit) {
        Map<Integer, ClientSum> clientsMap = clients.stream()
                .collect(Collectors.toMap(client -> client.clientId, client -> new ClientSum(client.clientId, client.name, 0.0)));
        PriorityQueue<ClientSum> topK = new PriorityQueue<>((a, b) -> {
            if (Double.compare(a.sum, b.sum) == 0) {
                return a.clientId - b.clientId;
            } else {
                return Double.compare(a.sum, b.sum);
            }
        });
        for (Order order: orders) {
            clientsMap.computeIfAbsent(order.clientId, key -> new ClientSum(order.clientId, null, 0.0));
            clientsMap.get(order.clientId).sum += order.total;
        }
        for (Map.Entry<Integer, ClientSum> entry: clientsMap.entrySet()) {
            topK.offer(entry.getValue());
            if (topK.size() > limit) {
                topK.poll();
            }
        }

        ClientSum[] result = new ClientSum[topK.size()];
        for (int i = topK.size() - 1; i >= 0; i--) {
            result[i] = topK.poll();
        }
        return Arrays.stream(result).toList();
    }

    @Test
    public void testGroupBy() {
        List<Order> orders = List.of(
                new Order(1000, 2000, "2024-03-08", 100.00),
                new Order(1001, 2000, "2024-03-08", 200.00),
                new Order(1002, 2001, "2024-03-08", 300.00),
                new Order(1003, 3000, "2024-03-08", 400.00)
        );

        List<Client> clients = List.of(
                new Client(2000, "Bo", "CA"),
                new Client(2001, "Anna", "CA"),
                new Client(2002, "Cat", "CA")
        );

        List<ClientSum> expected = List.of(
                new ClientSum(3000, null, 400.00),
                new ClientSum(2001, "Anna", 300.00),
                new ClientSum(2000, "Bo", 300.00)
        );

        List<ClientSum> grouped = groupByClients(orders, clients, 3);

        Assert.assertEquals(grouped, expected);

        List<ClientSum> expected1 = List.of(
                new ClientSum(3000, null, 400.00),
                new ClientSum(2001, "Anna", 300.00),
                new ClientSum(2000, "Bo", 300.00),
                new ClientSum(2002, "Cat", 0)
        );

        List<ClientSum> grouped1 = groupByClients(orders, clients, 5);

        Assert.assertEquals(grouped1, expected1);
    }
}

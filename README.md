# IfoodClone
Alunos: Fábio Shiniti Nakazato   RGA: 2017.1902.015-9

VISÃO GERAL:
O app possui o objtivo de recriar um clone do aplicativo de delivery de comida Ifood, onde há possibilidade para a seleção de itens pelos usuários através do cardápio
disponibilizado pelas empresas.

Os usuários podem cadastrar inicialmente um nome, e-mail e senha para efetuar o login, e depois do login precisam cadastrar o endereço para efetuar os pedidos. As empresas podem cadastrar
inicialmente um nome, e-mail e senha, e depois do login podem cadastrar uma foto, categoria, tempo de entrega e taxa de entrega. As empresas podem cadastrar ainda os produtos à serem fornecidos, cadastrando um nome, a descrição e o valor.
Todos os dados são salvos no banco de dados do Firebase, com excessão das senhas dos cadastros por motivos de segurança. O app é vinculado ao Firebase por meio do hash de segurança SHA1.

Após o login, na tela inicial do usuário é exibido uma listagem das empresas cadastradas, juntamente com o menu para entrar em configurações e ----. Após a seleção da empresa
desejada, é listado um cardápio com os dados de todos dos produtos cadastrados pela empresa. O usário pode selecionar um produto e adicionar a quantidade desejada. Todos as
quantidades são adicionadas de cada produto solicitadas são adicionadas em um “carrinho”, no qual exibe ao usuário a soma total das quantidades e o valor total. Por fim, o usuário
poderá confirmar um pedido e é exibido uma tela para selecionar a forma de pagamento (dinheiro ou cartão) e adicionar uma informação ao pedido. Assim, ao adicionar esta
informações, o pedido é salvo e exibido à empresa.

Na tela inicial da empresa, é exibido todos os seus produtos cadastrados, podendo estes serem excluídos. É exibido também os menus para cadastro de produto, configuração de dados
de cadastro da empresa e o menu de pedidos. Neste último menu, são listados todos os pedidos efetuados pelos usuários àquela empresa, podendo esta finalizar os pedidos.
 




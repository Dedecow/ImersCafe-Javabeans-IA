
# language: pt

Funcionalidade: Feedback do cliente

  Cenário: Exibir mensagem alternativa quando a IA estiver indisponível
    Dado que o jogador finalizou o atendimento
    Quando a integração com a DeepSeek estiver indisponível
    Então o sistema deve retornar uma mensagem de indisponibilidade

# Enunciado do Trabalho 1 - `jsonaif` via reflect

**Data limite de entrega: 11 de Abril**

Use como base do seu trabalho o projecto gradle disponível no repositório
`jsonaif`.

Pretende-se desenvolver a biblioteca `jsonaif` para processamento de dados em
formato JSON (https://www.json.org/).
Esta biblioteca disponibiliza um objecto `JsonParserReflect` que pode ser usado
para transformar uma _string_ JSON numa instância de uma classe de domínio
compatível (e.g. `Student`) conforme ilustrado no exemplo seguinte:

```kotlin
val json = "{ name: \"Ze Manel\", nr: 7353}"
val student = JsonParserReflect.parse(json, Student::class) as Student
assertEquals("Ze Manel", student.name)
assertEquals(7353, student.nr)
```

A class `JsonParserReflect` usa uma instância de uma classe auxiliar
`JsonTokens` para percorrer os elementos da _String_ JSON fonte.
O algoritmo de `JsonParserReflect` é recursivo, criando instâncias de classes de
domínio, ou uma lista, e preenchendo os seus campos, ou elementos, com valores
primitivos ou instâncias de outras classes de domínio, ou listas, e assim
sucessivamente.
Ambas as classes são fornecidas no projecto `jsonaif`.

**Este enunciado tem 5 partes, sendo que as partes 1 e 2 têm um grau de
dificuldade menor e uma dimensão mais reduzida do que cada uma das partes 3, 4
e 5.**

**A avaliação terá em conta não só a concretização com sucesso das 5 partes, mas
também o progresso dos alunos ao longo da realização do trabalho, incluindo o
que pode ser observado pelos commits no repositório git de suporte ao trabalho
do grupo**

## Parte 1

Implemente os métodos `parsePrimitive` e `parseObject` de `JsonParserReflect` de
modo a ter o comportamento desejado e satisfazer o testes unitário
`parseSimpleObjectViaProperties()` disponibilizado no projecto `jsonaif`.
Pode adicionar outros testes unitários além dos existentes.

Pode adicionar novos métodos auxiliares a `JsonParseReflect`. **A classe
`JsonTokens` não deve ser modificada.**

Implemente `parseObject` de `JsonParserReflect` instanciando a classe de domínio
através da chamada ao construtor sem parâmetros, ou que tem todos os
parâmetros opcionais (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.full/create-instance.html).
Posteriormente afecta cada um das propriedades vindas da string JSON. 

Note que nas classes de domínio dos testes do projecto `jsonaif` apenas a
classes `Student` pode ser instanciada nestas condições porque é a única que tem
um construtor com todos os parâmetros opcionais.

## Parte 2

Refaça a implementação de `JsonParserReflect` que mantenha uma estrutura de
dados com instâncias de `Setter` para cada classe de domínio, de modo a que não
seja repetido o trabalho de leitura de metadata via Reflexão.
Por exemplo, no parsing de um array de `Student` as propriedades a serem
afectadas só devem ser procuradas 1 vez.

A interface `Setter` especifica a forma de afectação de uma determinada
propriedade no parâmetro `target` a partir do valor obtido do parâmetro
`tokens`:
```kotlin
interface Setter {
    fun apply(target: Any, tokens: JsonTokens)
}
```

Por exemplo, na estrutura de dados seguinte cada classe de domínio é mapeada num
conjunto de pares: nome da propriedade - `Setter`

```kotlin
val setters = mutableMapOf<KClass<*>, Map<String, Setter>>()
```

## Parte 3

Altere a classe`JsonParserReflect` para suportar duas formas de instanciar a classe de domínio:

1. Através da chamada ao construtor sem parâmetros, ou que tem todos os
   parâmetros opcionais
   (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.full/create-instance.html).
   E.g. `Student`

2. Chamando um construtor com parâmetros. E.g. `Person`

A implementação de `parseObject`deve dar prioridade à opção 1, sempre que possível.

Implemente mais testes unitários incluindo por exemplo entidades de domínio como:

* `Classroom` que agrega um conjunto de instâncias de `Student`
* `Account` com um saldo (_balance_) e um conjunto de movimentos de conta
  (_transactions_)
* Um outro exemplo ao seu critério.

## Parte 4

Pretende-se que as propriedades da classe de domínio possam ter nomes distintos
dos nomes usados na representação em JSON.
Por exemplo, uma propriedade em JSON pode ter o nome `birth_date`  e em Kotlin
`birthDate`. Para resolver a correspondência entre propriedades de nome distinto
implemente uma anotação `JsonProperty` que possa ser usada sobre propriedades de
uma classe de domínio indicando o nome correspondente em JSON (e.g.
`@JsonProperty(“birth_date”)`).
Altere `JsonParserReflect` para implementar o comportamento especificado e
valide com testes unitários.

## Parte 5

Pretende-se ter uma forma alternativa de definir o valor de objectos sem ter que
seguir a sintaxe JSON.
Por exemplo, em vez de a propriedade `birth` de `Person`, do tipo
`pt.isel.sample.Date`, ser definida em JSON, como no exemplo seguinte, poderá
ter uma forma alternativa como  a que se apresenta para `Student`:
* JSON for a Person: `"{ name: "Ze Manel", birth: { year: 1999, month: 9, day: 19}, sibling: { name: "Kata Badala"}}"`
* JSON for a Student: `"{ name: "Maria Papoila", nr: 73753, birth: "1998-11-17" }"`

Neste caso a propriedade `birth` em `Student` tem que ter uma anotação que
identifique a classe responsável por fazer a conversão de `String` numa
instância de `Date`.
Exemplo:

```kotlin
data class Student (var nr: Int = 0, var name: String? = null, @JsonConvert(JsonToDate::class) val birth: Date)
```

Implemente uma forma de poder associar através da anotação `JsonConvert` um
conversor para qualquer classe de domínio.
`JsonParserReflect` deve passar a ter em consideração esta anotação na
inicialização das instâncias de `Setter`.

**Além do exemplo dado, valide a sua implementação com outro exemplo de conversor
para outro tipo de propriedade que deverá acrescentar a uma das classes de
domínio.**

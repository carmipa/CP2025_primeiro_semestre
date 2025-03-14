import { useState } from 'react';
import { Button, Image, StyleSheet, Text, TextInput, View } from 'react-native';

export default function App() {
  const [valorProduto, setValorProduto] = useState('');
  const [porcentagemAumento, setPorcentagemAumento] = useState('');
  const [monstrarDados, setMostrarDados] = useState(false);


  return (
    <View style={styles.container}>
      <Image

        source={{ uri: 'https://i.scdn.co/image/ab6765630000ba8a9543f1ed639f9830d951f154' }}
        style={styles.imagem}
      />
      <TextInput
        style={styles.input}
        placeholder='Digite o valor do produto:'
        maxLength={10}
        keyboardType='numeric'
        value={valorProduto}
        onChangeText={(valor) => setValorProduto(valor)}

      />
      <Text style={{ color: 'red' }}>{valorProduto}</Text>

      <TextInput
        style={styles.input}
        placeholder='Porcentagem de aumento:'
        maxLength={10}
        keyboardType='numeric'
        value={porcentagemAumento}
        onChangeText={(valor) => setPorcentagemAumento(valor)}

      />
      <Text style={{ color: 'red' }}>{porcentagemAumento}</Text>

      <Button title='Calcular'

        onPress={() => setMostrarDados(true)}
      />
      {monstrarDados && <Text>{valorProduto} - {porcentagemAumento}</Text>}



    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'black',
    alignItems: 'center',
    justifyContent: 'flex-start',
    gap: 10
  },
  imagem: {
    resizeMode: 'center',
    width: 300,
    height: 300
  },
  input: {
    backgroundColor: 'white',
    width: 400,
    borderRadius: 5,
    paddingLeft: 10,
    borderWidth: 2,
    borderColor: '#87DEEB',
    fontSize: 20
  }
});
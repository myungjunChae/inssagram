import torchvision.datasets as dset
import torchvision.transforms as transforms
import torch
import numpy as np
import time
import torch.optim as optim
import torch.nn as nn
from torchvision import models

batch_size = 32

class TrainImageFolder(dset.ImageFolder):
    def __getitem__(self, index):
        filename = str(self.imgs[index])
        filename = list(filename.split('_')[2:])
        filename[2] = filename[2].split('.')[0]
        for i in range(len(filename)):
            if filename[i] == '':
                print(str(self.imgs[index]))
            filename[i] = int(filename[i])
        filename = torch.FloatTensor(filename)
        return super(TrainImageFolder, self).__getitem__(index), filename

def trainDataset():
    dataset = TrainImageFolder(root="images_label/train",
                            transform=transforms.Compose([
                                transforms.RandomRotation(30),
                                transforms.RandomHorizontalFlip(),
                                transforms.RandomResizedCrop(680, scale=(0.96, 1.0), ratio=(0.95, 1.05)),
                                transforms.ToTensor(),
                                transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]),
                            ]))
    dataloader = torch.utils.data.DataLoader(dataset,
                                            batch_size=batch_size,
                                            shuffle=True,
                                            num_workers=4)
    return dataloader                               

def testDataset():
    dataset = TrainImageFolder(root="images_label/test",
                            transform=transforms.Compose([
                                transforms.Resize([680, 680]),
                                transforms.ToTensor(),
                                transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]),
                            ]))
    dataloader = torch.utils.data.DataLoader(dataset,
                                            batch_size=batch_size,
                                            shuffle=False,
                                            num_workers=4)
    return dataloader




# torch.cuda.set_device(7)

lr = 0.001
train_loader = trainDataset()
test_loader = testDataset()

model = models.resnet50(pretrained=True)
model = nn.Sequential(
    model,
    nn.Linear(1000, 100),
    nn.Linear(100, 2)
)

########################
# model.load_state_dict(torch.load('checkpoint/checpoint_epoch.pt'))
# model.eval()
########################
criterion = nn.MSELoss()

optimizer = optim.Adam(model.parameters(), lr=lr, weight_decay=1e-5)

def train(epoch):
    model.train()
    for idx, (data, filename) in enumerate(train_loader):
        data = data[0]

        like = filename[:,0] # (32)
        comment = filename[:,1]
        follower = filename[:,2]

        like_ratio = like/follower
        comment = torch.unsqueeze(comment, 1)
        like_ratio = torch.unsqueeze(like_ratio, 1)
        target = torch.cat([like_ratio, comment], 1)

        print(target.shape)

        optimizer.zero_grad()
        output = model(data)
        loss = criterion(output, target)
        loss.backward()
        optimizer.step()

        if idx % 10 == 0:
            print("epoch: ", epoch, "  process: ", int((idx / len(train_loader)) * 100),
                "%  Loss: ", loss.data.item())

        torch.save(model.state_dict(), 'checkpoint/checpoint_epoch'+str(epoch)+'.pt')

def test():
    model.eval()
    test_loss = 0
    acc_like = 0
    acc_comment = 0
    with torch.no_grad():
        for data, filename in test_loader:
           like = filename[:,0]
            comment = filename[:,1]
            follower = filename[:,2]
            target = torch.cat([like/follower, comment], 0)
            data = data[0]


            output = model(data)
            test_loss += criterion(output, target).data.item()
            acc_like += abs(output[0]*follower - like)
            acc_comment += abs(output[1] - comment)
    
    test_loss /= len(test_loader.dataset)
    acc_like /= len(test_loader.dataset)
    acc_comment /= len(test_loader.dataset)
    print("Average Loss: ", test_loss)
    print("Average Like Acc: ", acc_like)
    print("Average Comment Acc: ", acc_comment)


if __name__ == "__main__":
    for epoch in range(100):
        start = time.time()
        train(epoch)
        end = time.time()
        print("It takes ", end - start, " seconds")
        test()

import torchvision.datasets as dset
import torchvision.transforms as transforms
import torch
import numpy as np
import time
import torch.optim as optim
import torch.nn as nn
from torchvision import models
import argparse


batch_size = 1                             
def testDataset():
    dataset = dset.ImageFolder(root="demo",
                            transform=transforms.Compose([
                                transforms.Resize([680, 680]),
                                transforms.ToTensor(),
                                transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]),
                            ]))
    dataloader = torch.utils.data.DataLoader(dataset,
                                            batch_size=batch_size,
                                            shuffle=False,
                                            num_workers=0)
    return dataloader

test_loader = testDataset()
model = models.resnet50(pretrained=True)
model = nn.Sequential(
    model,
    nn.Linear(1000, 100),
    nn.Linear(100, 1)
)
model.load_state_dict(torch.load('checkpoint/checkpoint_epoch99.pt', map_location=torch.device('cpu')))
model.eval()

def test(follower):
    model.eval()
    with torch.no_grad():
        for data in test_loader:
            data = data[0]
            output = model(data)
            pred = round(output[0][0].item() * follower)
            print('like = ', pred)
            
    return pred

if __name__ == "__main__":
    test(345)